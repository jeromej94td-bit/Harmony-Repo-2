const CORS_HEADERS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
  'Access-Control-Allow-Methods': 'POST, OPTIONS',
}
const json = (body, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: { ...CORS_HEADERS, 'content-type': 'application/json; charset=utf-8' },
})

export function createDeleteAccountHandler({ fetchImpl = fetch, getServiceRole, getSupabaseUrl } = {}) {
  return async (request) => {
    if (request.method === 'OPTIONS') return new Response(null, { status: 204, headers: CORS_HEADERS })
    if (request.method !== 'POST') return json({ ok: false, error: 'method_not_allowed' }, 405)

    const serviceRole = getServiceRole?.()
    const baseUrl = getSupabaseUrl?.()
    if (!serviceRole || !baseUrl) return json({ ok: false, error: 'server_not_configured' }, 503)

    const authorization = request.headers.get('authorization') ?? ''
    if (!authorization.toLowerCase().startsWith('bearer ')) {
      return json({ ok: false, error: 'missing_authorization' }, 401)
    }

    const userResponse = await fetchImpl(`${baseUrl}/auth/v1/user`, {
      headers: { authorization, apikey: serviceRole },
    }).catch(() => null)
    if (!userResponse?.ok) return json({ ok: false, error: 'invalid_session' }, 401)

    const user = await userResponse.json().catch(() => null)
    if (!user?.id) return json({ ok: false, error: 'invalid_session' }, 401)

    const disconnectResponse = await fetchImpl(
      `${baseUrl}/rest/v1/rpc/disconnect_user_for_account_deletion`,
      {
        method: 'POST',
        headers: {
          authorization: `Bearer ${serviceRole}`,
          apikey: serviceRole,
          'content-type': 'application/json',
        },
        body: JSON.stringify({ p_user_id: user.id }),
      },
    ).catch(() => null)

    if (!disconnectResponse?.ok) {
      return json({ ok: false, error: 'couple_disconnect_failed' }, 502)
    }

    // Harmony profile photos are currently stored locally on-device. Optional cloud
    // avatar cleanup must never block irreversible account deletion.
    const deletionResponse = await fetchImpl(
      `${baseUrl}/auth/v1/admin/users/${encodeURIComponent(user.id)}`,
      {
        method: 'DELETE',
        headers: {
          authorization: `Bearer ${serviceRole}`,
          apikey: serviceRole,
        },
      },
    ).catch(() => null)

    if (!deletionResponse?.ok) return json({ ok: false, error: 'account_deletion_failed' }, 502)
    return json({ ok: true })
  }
}
