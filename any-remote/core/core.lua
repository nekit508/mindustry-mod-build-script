G.gdj()
info("Current root is " .. G.updater.root:absolutePath())
G.info("Start files processing? Y/n")

function process()
    G.info("processing")
end

resp = G.input()
if resp == "y" or resp == "Y" then
    process()
end