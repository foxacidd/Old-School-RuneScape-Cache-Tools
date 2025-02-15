package decoders;

import com.application.GUI;
import com.displee.cache.CacheLibrary;
import net.runelite.cache.definitions.ModelDefinition;
import net.runelite.cache.managers.TextureManagerHD;
import net.runelite.cache.models.ObjExporterHD;
import rshd.ModelData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public class ModelDecoderHD {

    CacheLibrary cacheLibrary;

    public ModelDecoderHD(GUI currentGUI) {
        cacheLibrary = GUI.cacheLibrary;
        int index = currentGUI.selectedIndex;
        int archive = currentGUI.selectedArchive;
        int file = currentGUI.selectedFile;
        try {
            File outputFilePath = new File(GUI.cacheLibrary.getPath() + File.separator + "Decoded Data" + File.separator + "Models");
            boolean madeDirectory = outputFilePath.mkdirs();
            if (madeDirectory) {
                GUI.cacheOperationInfo.setText("Archive " + archive + " was decoded successfully. New folder created in cache directory.");
            } else {
                GUI.cacheOperationInfo.setText("Archive " + archive + " was decoded successfully. It is in the cache directory.");
            }

            TextureManagerHD tm = new TextureManagerHD(cacheLibrary);
            tm.load();

            ModelData loader = new ModelData();
            ModelDefinition modelDefinition = loader.load(archive, Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(cacheLibrary.index(index).archive(archive)).file(file)).getData()));

            ObjExporterHD exporter = new ObjExporterHD(tm, modelDefinition, archive);
            try (PrintWriter objWriter = new PrintWriter(new FileWriter(outputFilePath + File.separator + archive + ".obj"));
                 PrintWriter mtlWriter = new PrintWriter(new FileWriter(outputFilePath + File.separator + archive + ".mtl")))
            {
                exporter.export(objWriter, mtlWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
