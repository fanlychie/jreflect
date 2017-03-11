package org.fanlychie.jreflect.exception;

/**
 * 运行时异常, 它内部包装了真实的非运行时异常对象, 通过 getCause 来取出真实异常
 * Created by fanlychie on 2017/3/3.
 */
public class ReflectionCastException extends RuntimeException {

    private Throwable cause;

    public ReflectionCastException(Throwable throwable) {
        this.cause = throwable;
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }

}