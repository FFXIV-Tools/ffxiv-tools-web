import Cookies from "universal-cookie";

const cookies = new Cookies();

export function authToken(): string | undefined {
    return getCookie("jwt");
}

export function getCookie(name: string): string | undefined {
    return cookies.get(name);
}

export function removeCookie(name: string) {
    return cookies.remove(name);
}
