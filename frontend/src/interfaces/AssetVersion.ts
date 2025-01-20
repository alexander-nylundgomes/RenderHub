import { AssetVersionQuality } from "../enums/asset-version-quality";
import { RenderStatus } from "../enums/render-status";

export interface AssetVersion {
    id: number,
    quality: AssetVersionQuality,
    extension: string,
    status: RenderStatus
    size: number,
    updatedAt: Date
}