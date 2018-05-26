package lt.dualpair.android.data;

public class RxErrorHandlingCallAdapterFactory /*extends CallAdapter.Factory*/ {
    /*private final RxJava2CallAdapterFactory original;

    private RxErrorHandlingCallAdapterFactory() {
        original = RxJava2CallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper implements CallAdapter<Observable<?>, Observable<?>> {
        private final Retrofit retrofit;
        private final CallAdapter<?, ?> wrapped;

        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<?> wrapped) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <?, ?> Observable<?> adapt(Call<?> call) {

        }

        @Override
        public <R, T> Observable<T> adapt(Call<Observable<R>> call) {
            return ((Observable) wrapped.adapt(call)).onErrorResumeNext(new Func1<Throwable, Observable>() {
                @Override
                public Observable call(Throwable throwable) {
                    return Observable.error(asServiceException(throwable));
                }
            });
        }

        private ServiceException asServiceException(Throwable throwable) {
            // We had non-200 http error
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                return ServiceException.httpError(response.raw().request().url().toString(), response, retrofit);
            }
            // A network error happened
            if (throwable instanceof IOException) {
                return ServiceException.networkError((IOException) throwable);
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return ServiceException.unexpectedError(throwable);
        }
    }*/
}

