import { ReactElement, useState } from "react";

function DropDownButton({ button, options }: { button: ReactElement, options: ReactElement }){
    
    const [open, setOpen] = useState<boolean>(false);
    
    const doToggle = () => {
        setOpen(wasOpen => !wasOpen);
    }

    return (
        <div className="relative">
            <div className="h-full" onClick={doToggle}>
                {button}
            </div>

            {
                open &&
                <div className="shadow h-auto bg-white rounded border absolute w-56 mt-2 ">
                    <ul className="text-left" onClick={doToggle}>{options}</ul>
                </div>
            }
        </div>
    )
}

export default DropDownButton;