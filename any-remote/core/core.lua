G.info("Current root is " .. G.updater.root:absolutePath())
G.info("Start files processing? Y/n")

function process()
    json = G.updater.jsonReader:parse(G.updater.root:child("updater.json"))
    value = json:get("repo"):asString()
    path = value:split("[\\\\/]", 3)
    repo = G.updater.github:getRepository(path[1] .. "/" .. path[2])

    files = repo:getDirectoryContent(path[3])

    for file in files do
        G.info(file)
    end
end

resp = G.input()
if resp == "y" or resp == "Y" then
    process()
end