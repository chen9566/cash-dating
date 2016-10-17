package me.jiangcai.dating;

import me.jiangcai.dating.entity.Bank;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author CJ
 */
public class BankNames {

    @Test
    public void changeName() throws IOException {
        Path banks = Paths.get(".", "src", "main", "webapp", "images", "banks");
        Path testDir = Paths.get(".", "target");
        Files.walk(banks)
                .filter(path -> Files.isRegularFile(path))
//                .filter(path -> path.endsWith(".png"))
                .filter(path -> path.getName(path.getNameCount() - 1).toString().endsWith(".png"))
                .peek(path -> {
                    String name = path.getName(path.getNameCount() - 1).toString();
                    name = name.substring(0, name.length() - 4);
                    System.out.println(Bank.toAsc(name));
                })
                .forEach(path -> {
                    String name = path.getName(path.getNameCount() - 1).toString();
                    name = name.substring(0, name.length() - 4);
                    try {
                        final String newName = Bank.toAsc(name);
                        if (!newName.equals(name))
                            Files.move(path, path.getParent().resolve(newName + ".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

}
