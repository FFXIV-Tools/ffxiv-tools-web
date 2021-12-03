import React, {useEffect, useRef, useState} from "react";

import Icon from "./Icon";

export type DropdownItem = {
    icon?: string,
    label?: string,
    onClick?: () => void,
};

export type DropdownProps = {
    buttonClassName?: string,
    className?: string,
    icon?: string,
    items: DropdownItem[],
    label?: string | JSX.Element,
};

const Dropdown = ({buttonClassName, className, icon, items, label}: DropdownProps) => {
    const [active, setActive] = useState<boolean>(false);
    const buttonRef = useRef<HTMLButtonElement>(null);

    function onDocumentClick(e: MouseEvent) {
        if (!buttonRef.current?.contains(e.target as Node)) {
            setActive(false);
        }
    }

    useEffect(() => {
        document.addEventListener("click", onDocumentClick)
        return () => document.removeEventListener("click", onDocumentClick);
    }, []);

    return <div className={`dropdown ${active ? "is-active" : ""} ${className || ""}`}>
        <div className="dropdown-trigger">
            <button
                className={`button ${buttonClassName || ""}`}
                aria-haspopup="true"
                onClick={() => setActive(!active)}
                ref={buttonRef}
            >
                {label && <span>{label}</span>}
                <Icon className="is-small" name={icon || "angle-down"}/>
            </button>
        </div>
        <div className="dropdown-menu" role="menu">
            <div className="dropdown-content">
                {items.map(({icon, label, onClick}, index) => {
                    if (!label) {
                        return <hr key={index} className="dropdown-divider"/>;
                    }

                    let children = icon
                        ? <><Icon name={icon}/> {label}</>
                        : label;

                    return <div
                        children={children}
                        className="dropdown-item"
                        key={index}
                        role="menuitem"
                        onClick={onClick}
                    />
                })}
            </div>
        </div>
    </div>;
};

Dropdown.divider = (): DropdownItem => ({});

export default Dropdown;
