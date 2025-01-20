import { useState } from "react";
import { AssetVersionQuality } from "../enums/asset-version-quality";
import readableDataSize from "../helpers/readable-data-size";
import { Asset } from "../interfaces/Asset";
import { AssetVersion } from "../interfaces/AssetVersion";
import DropDownButton from "./DropDownButton";
import "./File.css"
import { Icon } from "./Icon";
import Modal from "./Modal";
import axios from "axios";

function AssetCard({ asset, onAssetDelete }: { asset: Asset, onAssetDelete: () => void}){

    const thumbnailQuality: AssetVersionQuality = AssetVersionQuality.MEDIUM;
    const thumbnailAssetVersion: AssetVersion | undefined = asset.versions.find(version => version.quality == thumbnailQuality);
    
    const thumbnail: string = `${process.env.REACT_APP_BACKEND_URL}:${process.env.REACT_APP_BACKEND_PORT}/assets/${asset.id}/${thumbnailQuality}/stream?t=${thumbnailAssetVersion?.updatedAt}`;
    
    const originalVersion: AssetVersion | undefined = asset.versions.find(version => version.quality == AssetVersionQuality.ORIGINAL);
    const infoText: string = (originalVersion ? `${originalVersion.extension} - ${readableDataSize(originalVersion.size)}` : "Asset info not found");
    const [openDeleteModal, setOpenDeleteModal] = useState<boolean>(false);

    const deleteAsset = async () => {
        await axios.delete(`${process.env.REACT_APP_BACKEND_URL}:${process.env.REACT_APP_BACKEND_PORT}/assets/${asset.id}`);
        setOpenDeleteModal(false);
        onAssetDelete();
    }

    const qualities = Object.keys(AssetVersionQuality)

    return (
        <div className="file-wrapper aspect-file-card border p-2 border-blue-200 bg-white rounded-lg shadow w-full">
            
            <Modal
                openModal={openDeleteModal}
                header={<h1 className="text-blue-600">Are you sure?</h1>}
                body={<p className="p-4">You are about to delete an asset. This cannot be undone. Are you sure?</p>}
                footer={
                    <div className="buttons flex gap-2">
                        <button className="px-3 py-1 rounded text-sm shadow border border-blue-600 text-blue-600 ms-auto" onClick={() => setOpenDeleteModal(false)} >Cancel</button>
                        <button className="px-3 py-1 rounded text-sm shadow bg-red-500 text-white ms-auto" onClick={deleteAsset}>Yes, delete the asset</button>
                    </div>
                }
            />
            
            <div className="thumbnail-area p-2 bg-slate-200 aspect-square w-full">
                <img src={thumbnail} alt="" className="w-full max-h-full object-contain object-center" />
            </div>


            <div className="information flex gap-1 flex-col py-2">
                <h1 className="text-sm font-medium text-black text-ellipsis w-full overflow-hidden whitespace-nowrap">{asset.name}</h1>
                <p className="text-xs text-gray-700 font-medium">{infoText}</p>
            </div>
            
            <div className="buttons border-t pt-2 relative gap-2">
                
                <a 
                    className="px-3 py-1 w-full rounded text-sm shadow bg-blue-600 text-white flex items-center justify-center gap-1" 
                    download 
                    href={`${process.env.REACT_APP_BACKEND_URL}:${process.env.REACT_APP_BACKEND_PORT}/assets/${asset.id}/ORIGINAL/download`}
                >Download</a>
            
                
                <DropDownButton
                    button={
                        <button className="h-full aspect-square flex justify-center items-center rounded text-sm shadow border border-blue-600 text-blue-600">
                            <Icon className="text-blue-600 size-5" icon="More"></Icon>
                        </button>
                    }

                    options={
                        <>
                            {
                                qualities.map(quality => (
                                    <li key={quality} className="p-2 text-sm border-b">
                                        <a className="flex items-center justify-between" download href={`${process.env.REACT_APP_BACKEND_URL}:${process.env.REACT_APP_BACKEND_PORT}/assets/${asset.id}/${quality}/download`}>Download {quality.toLowerCase()} version<Icon className="text-black size-5" icon="Download"></Icon></a>
                                    </li>  
                                ))
                            }
                            
                            <li onClick={() => setOpenDeleteModal(true)} className="p-2 text-sm border-b flex items-center text-red-500 justify-between">Delete <Icon className="text-red-500 size-5" icon="Trash"></Icon></li>  
                        </>
                    }
                />
            </div>
        </div>
    )
}

export default AssetCard;