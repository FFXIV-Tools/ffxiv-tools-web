import React, {useEffect, useRef, useState} from "react";

export type DropdownItem = {
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
            <button
                children={label}
                className="button is-small"
                aria-haspopup="true"
                onClick={() => setActive(!active)}
                ref={buttonRef}
            />
        </div>
        <div className="dropdown-menu" role="menu">
            <div className="dropdown-content">
                {items.map(({label, onClick}, index) => {
                    if (!label) {
                        return <hr key={index} className="dropdown-divider"/>;
                    }

                    return <div
                        children={label}
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
