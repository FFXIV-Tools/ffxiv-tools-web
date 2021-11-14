import React from "react";

export type ModalProps = {
    active: boolean,
    close: () => void,
    children: JSX.Element | JSX.Element[],
};

const Modal = ({active, close, children}: ModalProps) => {
    return <div className={`modal ${active ? "is-active" : ""}`} onClick={close}>
        <div className="modal-background"/>
        {children}
        <button className="modal-close is-large" aria-label="close" onClick={close}>Close</button>
    </div>;
};

export default Modal;
