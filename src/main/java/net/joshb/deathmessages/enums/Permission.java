package net.joshb.deathmessages.enums;

public enum Permission {

    DEATHMESSAGES_COMMAND("deathmessages.command.deathmessages"),
    DEATHMESSAGES_COMMAND_TOGGLE("deathmessages.command.deathmessages.toggle"),
    DEATHMESSAGES_COMMAND_RELOAD("deathmessages.command.deathmessages.reload");

    private final String value;

    Permission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
