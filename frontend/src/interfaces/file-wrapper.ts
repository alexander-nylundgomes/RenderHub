export interface FileWrapper {
    file: File,
    thumbnail: string,
    randomId: number // Used to separate files from each other in loop,
    downloadProgress: number,
    name: string
}
