package zav.mc.gradle

import zav.mc.cd4code.Main
import java.nio.file.Path
import java.nio.file.Paths

generate("zav.discord.blanc.Architecture")
generate("zav.discord.blanc.activity.Activity")
generate("zav.discord.blanc.command.Command")
generate("zav.discord.blanc.exceptions.Exceptions")
generate("zav.discord.blanc.io.IO")
generate("zav.discord.blanc.parser.Parser")

def generate(String model){
    Path CURRENT = Paths.get("").toAbsolutePath();
    Path PARENT = CURRENT.getParent();

    Path MODELS_PATH = Paths.get("build", "codegen")
    Path LOCAL_MODELS_PATH = Paths.get("src", "main", "models")
    Path TEMPLATES_PATH = Paths.get("build", "codegen")
    Path LOCAL_TEMPLATES_PATH = Paths.get("src", "main", "templates")
    Path SOURCES_DIRECTORY = Paths.get("src", "main", "java")
    Path TARGET_DIRECTORY = Paths.get("build", "src", "main", "java")

    String[] args = new String[]{
            CURRENT.resolve(MODELS_PATH),
            CURRENT.resolve(LOCAL_MODELS_PATH),
            CURRENT.resolve(TEMPLATES_PATH),
            CURRENT.resolve(LOCAL_TEMPLATES_PATH),
            PARENT.resolve(SOURCES_DIRECTORY),
            CURRENT.resolve(TARGET_DIRECTORY),
            model
    }

    Main.main(args)
}
