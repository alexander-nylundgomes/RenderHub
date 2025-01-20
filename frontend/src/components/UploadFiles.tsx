import { useRef } from "react";
import { Icon } from "./Icon";
import UploadFileRow from "./UploadFileRow";
import { FileWrapper } from "../interfaces/file-wrapper";
import { useFileContext } from "../contexts/UploadFilesContext";

function UploadFiles() {
    const fileUploadRef = useRef<HTMLInputElement>(null);

    const { files, setFiles } = useFileContext();

    const randomNumber = () => Math.floor(Math.random() * 10000000);
    const doFileUpload = () => fileUploadRef.current?.click();

    const onFileUpload = async (fileList: FileList | null) => {

        let fileWrappers: FileWrapper[] = [];

        if (fileList == null) {
            fileWrappers = [];
        } else {
            let files: File[] = Array.from(fileList)

            for (let file of files) {
                
                const fileWrapper = { 
                    file, 
                    randomId: randomNumber(), 
                    thumbnail: window.URL.createObjectURL(file), 
                    downloadProgress: 0,
                    name: ""
                }

                fileWrappers.push(fileWrapper);
            }
        }

        setFiles((oldFileWrappers) => [...fileWrappers, ...oldFileWrappers]); // Allow the user to add files continuously
    }

    return (
        <div className="upload-files-wrapper p-4">

            <input type="file" multiple hidden accept="image/*" ref={fileUploadRef} onChange={(event) => { onFileUpload(event.target.files) }} />

            <div className="files-section mb-4 flex-col w-full p-4 flex items-center justify-center bg-slate-100 rounded border ">
                <Icon icon="Upload" className="size-8 text-slate-500"></Icon>
                <h1 className="text-slate-500 text-xl">Drop items here</h1>
                <h1 className="text-slate-500 text-sm py-4">or</h1>
                <button className="px-3 py-1 rounded text-sm border border-blue-600 text-blue-600" onClick={doFileUpload}>Browse files</button>
            </div>

            {/* <h1 className="text-sm mb-2">Ready for upload</h1> */}

            <div className="files-wrapper h-60 flex">
                {files.length == 0 &&
                    <p className="text-md text-slate-400 w-full my-auto text-center py-4">No files selected</p>
                }

                {files.length != 0 &&
                    <div className="files flex flex-col gap-4 w-full h-full overflow-auto">
                        {
                            files.map((fileWrapper) => (
                                <UploadFileRow key={fileWrapper.randomId} fileWrapper={fileWrapper}></UploadFileRow>
                            ))
                        }
                    </div>
                }
            </div>
        </div>
    )
}

export default UploadFiles;