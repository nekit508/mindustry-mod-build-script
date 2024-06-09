if G == nil then
    G = {}
end

function G.bind(className)
    return luajava.bindClass(className)
end

function G.input()
    return G.updater.reader:readLine()
end

function G.info(message)
    G.log:info(message, nil)
end

function G.err(message)
    G.log:err(message)
end

function G.warn(message)
    G.log:warn(message)
end

G.log = G.bind("arc.util.Log")
G.updater = G.bind("nekit508.updater.Updater")

