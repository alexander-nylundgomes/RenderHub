import { ReactNode, useEffect, useRef } from "react";
import "./Modal.css"
import { Icon } from "./Icon";

function Modal({ openModal, header, body, footer }: { openModal: boolean, header: ReactNode, body: ReactNode, footer: ReactNode }){

    const ref = useRef<HTMLDialogElement>(null);
    
    useEffect(() => {
        if(openModal){
            ref.current?.showModal();
        }else{
            ref.current?.close();
        }
    }, [openModal]);

    return (
        <dialog className="backdrop:bg-black/25 w-1/2  rounded-lg" ref={ref}>
            <div className="header w-full p-2 border-b flex items-center">
                {header}
                <Icon className="text-black/50 size-5 ms-auto" icon="Close"></Icon>
            </div>

            <div className="body">
                {body}
            </div>

            <div className="footer w-full p-2 border-t">
                {footer}
            </div>
        </dialog>
    )
}

export default Modal;