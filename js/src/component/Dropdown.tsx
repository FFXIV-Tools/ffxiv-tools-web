import React, {useEffect, useRef, useState} from "react";

export type DropdownProps = {
    children: JSX.Element | JSX.Element[],
    label: string | JSX.Element | JSX.Element[],
};

const Dropdown = ({children, label}: DropdownProps) => {
    const [active, setActive] = useState<boolean>(false);
    const buttonRef = useRef<HTMLButtonElement>(null);

    function onButtonClick() {
        setActive(!active);
    }

    function onDocumentClick(e: MouseEvent) {
        if (e.target !== buttonRef.current) {
            setActive(false);
        }
    }

    useEffect(() => {
        document.addEventListener("click", onDocumentClick)
        return () => document.removeEventListener("click", onDocumentClick);
    }, []);

    return <div className={`dropdown ${active ? "is-active" : ""}`}>
        <div className="dropdown-trigger">
            <button className="button is-small" onClick={onButtonClick} ref={buttonRef}>
                {label}
            </button>
        </div>
        <div className="dropdown-menu" role="menu">
            <div className="dropdown-content">{children}</div>
        </div>
    </div>;
};

export default Dropdown;
