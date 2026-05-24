package com.example.weddingpartnerapp.common;
public interface ThrowExcepFunction<T,R> {
	R apply(T t) throws ApplicationException;
}
