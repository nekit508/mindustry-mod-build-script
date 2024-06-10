G.info("Current root is " .. G.updater.root:absolutePath())
G.info("Start files processing? Y/n")

function process()
    json = G.updater.jsonReader:parse(G.updater.root:child("updater.json"))
    repoPath = json:get("repo"):asString()
    dir = json:get("dir"):asString()

    repo = G.updater.gitHub:getRepository(repoPath)
    files = repo:getDirectoryContent(dir)

    for file in files do
        G.info(file)
    end
end

resp = G.input()
if resp == "y" or resp == "Y" then
    process()
end

