from pathlib import Path
import math
import bpy

REPO = Path(__file__).resolve().parents[2]
OUTPUT = REPO / "app" / "src" / "main" / "assets" / "models" / "harmony_magic_book.glb"
OUTPUT.parent.mkdir(parents=True, exist_ok=True)

bpy.ops.object.select_all(action="SELECT")
bpy.ops.object.delete(use_global=False)
for datablocks in (bpy.data.materials, bpy.data.curves, bpy.data.meshes):
    for datablock in list(datablocks):
        if datablock.users == 0:
            datablocks.remove(datablock)

scene = bpy.context.scene
scene.frame_start = 1
scene.frame_end = 70
scene.render.fps = 30


def material(name, base, metallic=0.0, roughness=0.45, emission=None, strength=0.0):
    mat = bpy.data.materials.new(name)
    mat.use_nodes = True
    bsdf = mat.node_tree.nodes.get("Principled BSDF")
    bsdf.inputs["Base Color"].default_value = (*base, 1.0)
    bsdf.inputs["Metallic"].default_value = metallic
    bsdf.inputs["Roughness"].default_value = roughness
    if emission is not None:
        emission_color = bsdf.inputs.get("Emission Color") or bsdf.inputs.get("Emission")
        if emission_color is not None:
            emission_color.default_value = (*emission, 1.0)
        emission_strength = bsdf.inputs.get("Emission Strength")
        if emission_strength is not None:
            emission_strength.default_value = strength
    return mat


def cube(name, location, dimensions, mat, bevel=0.05):
    bpy.ops.mesh.primitive_cube_add(location=location)
    obj = bpy.context.object
    obj.name = name
    obj.dimensions = dimensions
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    if bevel > 0:
        mod = obj.modifiers.new(name="Soft bevel", type="BEVEL")
        mod.width = bevel
        mod.segments = 3
        bpy.context.view_layer.objects.active = obj
        bpy.ops.object.modifier_apply(modifier=mod.name)
    obj.data.materials.append(mat)
    return obj


def parent_keep_world(obj, parent):
    world = obj.matrix_world.copy()
    obj.parent = parent
    obj.matrix_world = world
cover_mat = material("Royal violet leather", (0.055, 0.008, 0.12), metallic=0.05, roughness=0.28)
gold_mat = material("Warm gold", (0.83, 0.42, 0.07), metallic=0.92, roughness=0.20)
paper_mat = material("Warm ivory pages", (0.91, 0.72, 0.46), metallic=0.0, roughness=0.72)
paper_edge_mat = material("Golden page edge", (0.62, 0.29, 0.04), metallic=0.65, roughness=0.30)
gem_mat = material(
    "Heart gem",
    (0.34, 0.01, 0.12),
    metallic=0.08,
    roughness=0.18,
    emission=(1.0, 0.01, 0.32),
    strength=4.5,
)

root = bpy.data.objects.new("HarmonyMagicBook", None)
bpy.context.collection.objects.link(root)

back_cover = cube("BackCover", (0.0, 0.0, -0.22), (3.45, 2.35, 0.13), cover_mat, 0.10)
page_block = cube("PageBlock", (0.03, 0.0, -0.02), (3.16, 2.10, 0.31), paper_mat, 0.07)
page_edge = cube("PageGoldEdge", (1.63, 0.0, -0.02), (0.055, 2.04, 0.28), paper_edge_mat, 0.02)
spine = cube("Spine", (-1.69, 0.0, -0.03), (0.18, 2.32, 0.47), cover_mat, 0.07)
for obj in (back_cover, page_block, page_edge, spine):
    obj.parent = root

hinge = bpy.data.objects.new("CoverHinge", None)
hinge.location = (-1.69, 0.0, 0.18)
hinge.parent = root
bpy.context.collection.objects.link(hinge)
front_cover = cube("FrontCover", (0.0, 0.0, 0.22), (3.45, 2.35, 0.13), cover_mat, 0.10)
parent_keep_world(front_cover, hinge)

for name, loc, dims in (
    ("GoldTop", (0.0, 1.105, 0.30), (3.08, 0.055, 0.045)),
    ("GoldBottom", (0.0, -1.105, 0.30), (3.08, 0.055, 0.045)),
    ("GoldLeft", (-1.53, 0.0, 0.30), (0.055, 2.15, 0.045)),
    ("GoldRight", (1.53, 0.0, 0.30), (0.055, 2.15, 0.045)),
):
    trim = cube(name, loc, dims, gold_mat, 0.018)
    parent_keep_world(trim, hinge)

bpy.ops.mesh.primitive_ico_sphere_add(subdivisions=3, radius=0.31, location=(0.15, 0.0, 0.39))
gem = bpy.context.object
gem.name = "HarmonyGem"
gem.scale = (0.78, 1.0, 0.28)
bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
gem.data.materials.append(gem_mat)
parent_keep_world(gem, hinge)

for x in (-0.55, 0.15, 0.85):
    flourish = cube(f"GoldFlourish_{x:+.2f}", (x, 0.0, 0.315), (0.42, 0.035, 0.025), gold_mat, 0.01)
    flourish.rotation_euler[2] = math.radians(28 if x < 0 else -28)
    parent_keep_world(flourish, hinge)
for index in range(7):
    z = 0.105 + index * 0.018
    page = cube(
        f"PageLayer_{index:02d}",
        (0.04 + index * 0.006, 0.0, z),
        (3.05 - index * 0.012, 2.02 - index * 0.010, 0.012),
        paper_mat,
        0.012,
    )
    page.parent = root

hinge.rotation_mode = "XYZ"
hinge.animation_data_create()
action = bpy.data.actions.new("HarmonyBookOpen")
hinge.animation_data.action = action

for frame, degrees in ((1, 0.0), (16, -8.0), (42, -112.0), (58, -156.0), (70, -160.0)):
    scene.frame_set(frame)
    hinge.rotation_euler[1] = math.radians(degrees)
    hinge.keyframe_insert(data_path="rotation_euler", index=1)

for curve in action.fcurves:
    for point in curve.keyframe_points:
        point.interpolation = "BEZIER"

scene.frame_set(1)
bpy.ops.export_scene.gltf(
    filepath=str(OUTPUT),
    export_format="GLB",
    export_animations=True,
    export_yup=True,
    export_apply=True,
    export_nla_strips=False,
)

print(f"EXPORTED_GLB={OUTPUT}")
print(f"GLB_BYTES={OUTPUT.stat().st_size}")
print(f"OBJECT_COUNT={len(bpy.context.scene.objects)}")
print("ACTIONS=" + ",".join(action.name for action in bpy.data.actions))
