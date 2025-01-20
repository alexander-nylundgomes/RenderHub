import { useState } from "react";
import Modal from "./Modal";
import UploadFiles from "./UploadFiles";
import { Icon } from "./Icon";
import { useFileContext } from "../contexts/UploadFilesContext";
import axios from "axios";
import { FileWrapper } from "../interfaces/file-wrapper";

function ActionBar({ onRefresh }: { onRefresh: () => void}) {

    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const { files } = useFileContext();
    const [sendingFiles, setIsSendingFiles] = useState<boolean>(false);

    const onUpload = async () => {

        setIsSendingFiles(true);

        for(const fileWrapper of files){
            await sendFileToServer(fileWrapper);
        }

        setIsSendingFiles(false);
        setModalOpen(false);
        onRefresh();
    }

    const sendFileToServer = (fileWrapper: FileWrapper) => {
        const data = new FormData();
        data.append("file", fileWrapper.file);
        data.append("name", fileWrapper.name);

        return axios.post(`${process.env.REACT_APP_BACKEND_URL}:${process.env.REACT_APP_BACKEND_PORT}/assets`, data, {
            onUploadProgress: progressEvent => {
                console.log('Progress ' + progressEvent.progress, progressEvent);
            }
        })
    }

    return (
        <div className="action-bar-wrapper flex">
            <Modal
                openModal={modalOpen}
                header={
                    <h1 className="text-blue-600">Upload files</h1>
                }
                body={
                    <div className="min-h-80">
                        <UploadFiles />
                    </div>
                }
                footer={
                    <div className="buttons flex gap-2">
                        <button className="px-3 py-1 rounded text-sm shadow border border-blue-600 text-blue-600 ms-auto" onClick={() => { setModalOpen(false) }} >Cancel</button>
                        <button
                            disabled={sendingFiles}
                            className="px-3 py-1 rounded text-sm shadow bg-blue-600 text-white ms-auto"
                            onClick={onUpload}
                        >Upload</button>
                    </div>
                }
            />

            <button className="px-3 py-1 rounded text-sm border border-blue-600 text-blue-600 flex gap-2 items-center" onClick={onRefresh}>
                Refresh workspace <Icon className="size-4" icon="Refresh"></Icon>
            </button>

            <button
                className="px-3 py-1 rounded text-sm shadow bg-blue-600 text-white ms-auto"
                onClick={() => { setModalOpen(true) }}
            >Upload files</button>
        </div>
    )
}

export default ActionBar;