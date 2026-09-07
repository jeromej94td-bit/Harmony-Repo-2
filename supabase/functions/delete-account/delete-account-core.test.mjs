import test from 'node:test'
import assert from 'node:assert/strict'
import { createDeleteAccountHandler } from './delete-account-core.mjs'

test('missing optional cloud avatar must not block account deletion', async () => {
  const calls = []
  const fetchImpl = async (url, init = {}) => {
    calls.push({ url, init })
    if (url.endsWith('/auth/v1/user')) {
      return new Response(JSON.stringify({ id: 'user-123' }), { status: 200 })
    }
    if (url.endsWith('/rest/v1/rpc/disconnect_user_for_account_deletion')) {
      return new Response('{}', { status: 200 })
    }
    if (url.includes('/storage/v1/object/harmony-avatars/')) {
      return new Response(JSON.stringify({ message: 'Object not found' }), { status: 400 })
    }
    if (url.endsWith('/auth/v1/admin/users/user-123')) {
      return new Response('{}', { status: 200 })
    }
    throw new Error(`Unexpected URL: ${url}`)
  }

  const handler = createDeleteAccountHandler({
    fetchImpl,
    getServiceRole: () => 'service-role-secret',
    getSupabaseUrl: () => 'https://example.supabase.co',
  })

  const response = await handler(new Request('https://edge.example/delete-account', {
    method: 'POST',
    headers: { authorization: 'Bearer user-access-token' },
  }))
  const body = await response.json()

  assert.equal(response.status, 200)
  assert.deepEqual(body, { ok: true })
  assert.equal(
    calls.some(({ url }) => url.endsWith('/auth/v1/admin/users/user-123')),
    true,
    'auth user deletion must still run when no optional cloud avatar exists',
  )
})
