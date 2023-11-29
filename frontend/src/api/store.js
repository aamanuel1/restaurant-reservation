import { configureStore, getDefaultMiddleware } from "@reduxjs/toolkit";
import { setupListeners } from "@reduxjs/toolkit/query";
import { usersApi } from "./usersSlice";
import { reducer as formReducer } from "redux-form";


export const store = configureStore({
    reducer: {
        [usersApi.reducerPath]: usersApi.reducer,
        form: formReducer,
    },
    middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(usersApi.middleware),
})

setupListeners(store.dispatch);