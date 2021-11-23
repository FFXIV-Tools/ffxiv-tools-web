import React, {useEffect, useRef, useState} from "react";

import Icon from "./Icon";

export type DropdownItem = {
    icon?: string,
    label?: string,
    onClick?: () => void,
};

export type DropdownProps = {
    items: DropdownItem[],
    label: string | JSX.Element | JSX.Element[],
};

const Dropdown = ({items, label}: DropdownProps) => {
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

    return <div className={`dropdown ${active ? "is-active" : ""}`}>
        <div className="dropdown-trigger">
            <button
                className="button is-small"
                aria-haspopup="true"
                onClick={() => setActive(!active)}
                ref={buttonRef}
            >
                <span>{label}</span>
                <Icon className="is-small" name="angle-down"/>
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
