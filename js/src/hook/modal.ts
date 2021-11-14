import {useState} from "react";

export const useModal = (initial: boolean = false): [boolean, () => void, () => void] => {
    const [active, setActive] = useState(initial);

    return [active, () => setActive(true), () => setActive(false)];
};
