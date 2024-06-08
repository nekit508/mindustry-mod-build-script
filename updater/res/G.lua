function G.java(className)
    return luajava.bindClass(className)
end

function G.inst(className, args)
    return luajava.newInstance(className, args)
end

function G.input()
    return G.stdin:readLine()
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

G.log = G.java("arc.util.Log")
G.updater = G.java("nekit508.updater.Updater")