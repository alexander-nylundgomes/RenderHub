import { createContext, useContext, useState } from "react";
import { FileWrapper } from "../interfaces/file-wrapper";

interface FileContextType {
    files: FileWrapper[];
    setFiles: React.Dispatch<React.SetStateAction<FileWrapper[]>>;
    updateFileWrapperName: (randomId: number, name: string) => void;
    removeFileWrapper: (randomId: number) => void
}

const FileContext = createContext<FileContextType | undefined>(undefined);

export const FileUploadProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [files, setFiles] = useState<FileWrapper[]>([]);

    const updateFileWrapperName = (randomId: number, name: string) => {
        setFiles((prevFiles) => prevFiles.map((file) =>file.randomId === randomId ? { ...file, name } : file));
    };

    const removeFileWrapper = (randomId: number) => {
        setFiles((prevFiles) => prevFiles.filter(file => file.randomId !== randomId));
    }

    return (
        <FileContext.Provider value={{ files, setFiles, updateFileWrapperName, removeFileWrapper }}>
            {children}
        </FileContext.Provider>
    );
};

export const useFileContext = (): FileContextType => {
    const context = useContext(FileContext);
    if (!context) {
        throw new Error('useFileContext must be used within a FileProvider');
    }
    return context;
};