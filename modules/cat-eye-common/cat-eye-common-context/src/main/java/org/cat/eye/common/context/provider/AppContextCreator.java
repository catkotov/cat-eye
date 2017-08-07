package org.cat.eye.common.context.provider;

public interface AppContextCreator {

    SpringContextProvider createContext() throws Exception;

}
