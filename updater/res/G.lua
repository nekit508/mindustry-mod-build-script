G = {}

function G.java(className)
    return luajava.bindClass(className)
end

function G.inst(className, args)
    return luajava.newInstance(className, args)
end