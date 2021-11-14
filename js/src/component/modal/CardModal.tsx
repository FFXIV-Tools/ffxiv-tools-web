import React from "react";
import Modal, {ModalProps} from "./index";

export type CardModalProps = ModalProps & {
    footer?: JSX.Element | JSX.Element[],
    title: string,
};

const CardModal = ({active, close, children, footer, title}: CardModalProps) => {
    return <Modal active={active} close={close}>
        <div className="modal-card" onClick={e => e.stopPropagation()}>
            <header className="modal-card-head">
                <div className="modal-card-title">{title}</div>
                <button className="delete" aria-label="close" onClick={close}/>
            </header>
            <section className="modal-card-body">{children}</section>
            {footer && <footer className="modal-card-foot">{footer}</footer>}
        </div>
    </Modal>
};

export default CardModal;
