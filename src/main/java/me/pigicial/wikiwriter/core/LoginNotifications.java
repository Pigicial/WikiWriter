package me.pigicial.wikiwriter.core;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import lombok.Data;
import me.pigicial.wikiwriter.WikiWriter;
import net.minecraft.client.Minecraft;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class LoginNotifications {

    public static void sendLoginNotification()  {
        new Thread(() -> {
            try {
                HttpPost post = new HttpPost("https://discord.com/api/webhooks/905261464342896671/OEl7f8Iq58GCoJznZ38-mDRNLDXMFDeBCFlfTOpQ3bjldwK0lA7WorLHq27SCEE-5syU");
                post.setHeader("Content-Type", "application/json");

                GameProfile profile = Minecraft.getMinecraft().getSession().getProfile();
                String string = new Gson().toJson(new EmbedWrapper(new Embed[]{new Embed(profile)}));
                post.setEntity(new StringEntity(string));

                HttpClients.createDefault().execute(post);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Data
    private static class EmbedWrapper {
        private final Embed[] embeds;
    }

    @Data
    private static class Embed {
        private final String title;
        private final String description;
        private final int color;
        private final String timestamp;
        private final Footer footer;
        private final Image thumbnail;
        private final Image image;
        private final Author author;
        private final List<JsonField> fields;

        public Embed(GameProfile profile) {
            String uuid = profile.getId().toString();
            this.title = "User **" + profile.getName() + "** just logged in!";
            this.description = "wee woo wee woo";
            this.color = 7903378;
            this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Instant.now().toEpochMilli());
            String avatar = "https://crafatar.com/avatars/" + uuid + "?&overlay";
            this.footer = new Footer(avatar, "Swag Money");
            this.thumbnail = new Image(avatar);
            this.image = new Image(avatar);
            this.author = new Author(profile.getName(), avatar);
            this.fields = Arrays.asList(new JsonField("Username", profile.getName()), new JsonField("UUID", uuid), new JsonField("Timestamp", this.timestamp), new JsonField("Mod Version", WikiWriter.VERSION));
        }

    }

    @Data
    private static class Image {
        private final String url;
    }

    @Data
    private static class Author {
        private final String name;
        private final String icon_url;
    }

    @Data
    private static class Footer {
        private final String icon_url;
        private final String text;
    }

    @Data
    private static class JsonField {
        private final String name;
        private final String value;
    }
}
