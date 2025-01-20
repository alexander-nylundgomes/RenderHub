import { useState } from "react";
import { useFileContext } from "../contexts/UploadFilesContext";
import readableDataSize from "../helpers/readable-data-size";
import { FileWrapper } from "../interfaces/file-wrapper";
import { Icon } from "./Icon";
import "./UploadFileRow.css";

function UploadFileRow({ fileWrapper }: { fileWrapper: FileWrapper }) {
    const { removeFileWrapper, updateFileWrapperName } = useFileContext();
    const [filename, setFileName ] = useState(fileWrapper.name);


    const handleNameChange = (value: string) => {
        setFileName(value);
        updateFileWrapperName(fileWrapper.randomId, value);
    }

    return (
        <div className="upload-file-row-wrapper w-full border rounded">
            <div className="file-row  gap-2 p-2 ">
            
                <div className="thumbnail h-full aspect-square bg-slate-100 border relative">
                    <img src={fileWrapper.thumbnail ? fileWrapper.thumbnail : ""} className="w-full p-1 absolute h-full object-contain object-center" alt="" />
                </div>
                
                <div className="asset-name-input-group w-full flex flex-col gap-1">
                    <div className="flex items-center gap-4">
                        <label className="text-sm whitespace-nowrap">Asset name</label>
                        <p className="text-xs ms-auto text-slate-500 text-ellipsis overflow-hidden whitespace-nowrap">{fileWrapper.file.name} - {readableDataSize(fileWrapper.file.size)}</p>
                        <button className="">
                            <Icon className="size-4" onClick={() => removeFileWrapper(fileWrapper.randomId)} icon="Close"></Icon>
                        </button>
                    </div>
                    <input type="text" placeholder="My image" value={filename} onChange={(event) => handleNameChange(event.target.value)} className="border w-full py-1 px-2 text-sm rounded" />
                </div>

            </div>

            {/* <div className="progress w-full p-2 bg-slate-100 border-t flex">
                {fileWrapper.downloadProgress == 0 && 
                    <p className="text-xs text-blue-600 w-full text-center">Waiting for download</p>
                }

                {fileWrapper.downloadProgress > 0 && 
                    <progress className="w-full h-3 border border-blue-600 rounded overflow-hidden" value={fileWrapper.downloadProgress} max="100"></progress>
                }

            </div> */}
        </div>
    )
}

export default UploadFileRow;