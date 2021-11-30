import {createContext, Dispatch, useContext, useEffect, useReducer} from "react";
import {getCurrentAccount} from "../action/account";

type GlobalContext = {
    account?: Account,
};

const context = createContext<[GlobalContext, Dispatch<GlobalContext>]>([{}, () => true]);

const reducer = (state: GlobalContext, action: GlobalContext) => {
    return {...state, ...action};
};

export const useGlobalContext = (): [GlobalContext, Dispatch<GlobalContext>] => {
    const [state, dispatch] = useReducer(reducer, {});

    useEffect(() => {
        (async () => {
            const account = await getCurrentAccount();
            dispatch({account});
        })();
    }, []);

    return [state, dispatch];
};

const buildUseContext = <K extends keyof GlobalContext>(attribute: K) => {
    type V = GlobalContext[K];

    return (): [V, (arg0: V) => void] => {
        const [state, dispatch] = useContext(context);
        return [
            state[attribute],
            (v: V) => dispatch({[attribute]: v})
        ];
    };
};

export const useAccount = buildUseContext("account");

export const GlobalContextProvider = context.Provider;
