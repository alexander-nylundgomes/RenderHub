import { AssetVersion } from "./AssetVersion";

export interface Asset {
    id: number,
    name: string,
    description: string,
    versions: AssetVersion[]
}