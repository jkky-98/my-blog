export type ApiError = {
  code: string
  message: string
  fieldErrors?: Record<string, string>
}

export async function delay<T>(value: T) {
  await new Promise(resolve => window.setTimeout(resolve, 80))
  return value
}

export function toApiError(error: unknown): ApiError {
  if (error instanceof Error) {
    return {
      code: 'MOCK_ERROR',
      message: error.message,
    }
  }

  return {
    code: 'UNKNOWN_ERROR',
    message: '요청을 처리하지 못했습니다.',
  }
}
