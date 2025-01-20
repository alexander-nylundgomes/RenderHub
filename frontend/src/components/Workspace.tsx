import { useEffect, useState } from "react";
import ActionBar from "./ActionBar";
import axios from "axios";
import { Asset } from "../interfaces/Asset";
import AssetCard from "./AssetCard";
import { FileUploadProvider } from "../contexts/UploadFilesContext";

function Workspace() {

    const [assets, setAssets] = useState<Asset[]>([]);

    const fetchAssets = () => {
        axios.get<Asset[]>(`${process.env.REACT_APP_BACKEND_URL}:${process.env.REACT_APP_BACKEND_PORT}/assets`).then(response => {
            setAssets(response.data);
        }).catch((error) => {
            console.error('Failed fetching assets!', error)
        })
    }

    useEffect(() => fetchAssets, [])

    return (
        <FileUploadProvider>

            <div className="workspace-wrapper w-full h-full border border-blue-200 rounded-lg overflow-hidden flex flex-col">
                <div className="rounded-t-lg border-b border-blue-200 p-3">
                    <ActionBar
                        onRefresh={fetchAssets}
                    ></ActionBar>
                </div>

                <div className="workspace w-full h-full bg-blue-100/50 gap-3 p-2 grid grid-cols-5 relative">
                    {assets.map((asset) => (
                        <AssetCard key={asset.id} asset={asset} onAssetDelete={fetchAssets}></AssetCard>
                    ))}

                    {assets.length == 0 && 
                        <h1 className="w-full h-full flex items-center justify-center text-center absolute ">No assets in workspace</h1>
                    }
                </div>

                <div className="border-t rounded-b-lg p-3 border-blue-200">
                    <h1>Total assets: {assets.length}</h1>
                </div>
            </div>
        </FileUploadProvider>
    )
}

export default Workspace;