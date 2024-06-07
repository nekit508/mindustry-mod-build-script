G = {}

function G.java(className)
    return luajava.bindClass(className)
end

function G.inst(className, args)
    return luajava.newInstance(className, args)
end

function G.waitLineInput()
    return G.stdinReader:readLine()
end

function G.info(message)
    G.log:info(message)
end

function G.err(message)
    G.log:err(message)
end

function G.warn(message)
    G.log:warn(message)
end

G.stdinReader = G.inst("java.io.BufferedReader", STDIN)
G.log = G.java("arc.utils.Log")