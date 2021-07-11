package org.cbm.ant.cc65;

public enum Target
{

    C64("c64", new Library("c64.lib"));

    private final String name;
    private final Library[] libraries;

    Target(String name, Library... libraries)
    {
        this.name = name;
        this.libraries = libraries;
    }

    public String getName()
    {
        return name;
    }

    public Library[] getLibraries()
    {
        return libraries;
    }

}
