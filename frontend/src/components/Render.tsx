import "./Render.css"

function Render(){

    return (
        <div className="render-wrapper flex w-full p-2 h-min border-b border-slate-300">
          <div className="original-image">
            <img src="/1.jpeg" className="h-20" alt="" />
            
          </div>

          <div className="render-instruction">

          </div>

          <div className="output-image">
            <img src="/1.jpeg" className="h-20" alt="" />
          </div>
        </div>
    )
}

export default Render;