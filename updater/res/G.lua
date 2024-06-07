function java(className)
    return luajava.bindClass(className)
end

function inst(className, args)
    return luajava.newInstance(className, args)
end