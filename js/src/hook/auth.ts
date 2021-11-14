import {useState} from "react";
import {authToken, removeCookie} from "../util/cookie";

export function useLoginStatus(): [boolean, () => void] {
    const [loggedIn, setLoggedIn] = useState<boolean>(!!authToken());

    return [loggedIn, () => {
        removeCookie("jwt");
        setLoggedIn(false);
    }];
}
