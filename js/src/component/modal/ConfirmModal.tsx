import CardModal, {CardModalProps} from "./CardModal";

export type ConfirmModalProps = Omit<CardModalProps, "footer"> & {
    onNo?: () => void,
    onYes: () => any,
};

const ConfirmModal = ({children, close, onNo, onYes, ...props}: ConfirmModalProps) => {
    function onYesClick() {
        onYes();
        close();
    }

    let footer = <>
        <button className="button is-primary" onClick={onYesClick}>Yes</button>
        <button className="button" onClick={onNo || close}>No</button>
    </>;
    return <CardModal {...props} close={close} footer={footer}>{children}</CardModal>
};

export default ConfirmModal;
