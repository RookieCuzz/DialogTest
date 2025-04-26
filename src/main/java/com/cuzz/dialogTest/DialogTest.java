package com.cuzz.dialogTest;

import kr.toxicity.hud.api.BetterHud;
import kr.toxicity.hud.api.BetterHudAPI;
import kr.toxicity.hud.api.bukkit.event.CustomPopupEvent;
import kr.toxicity.hud.api.bukkit.update.BukkitEventUpdateEvent;
import kr.toxicity.hud.api.player.HudPlayer;
import kr.toxicity.hud.api.popup.Popup;
import kr.toxicity.hud.api.popup.PopupUpdater;
import kr.toxicity.hud.player.HudPlayerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class DialogTest extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
    }
    // 将字符串按字符拆分为一个字符串列表
    public static List<String> spiltX(String input) {
        List<String> src = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            src.add(input.substring(0, i + 1));  // 每次添加从0到当前字符的子字符串
        }

        return src;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (message.equals("测试消息")) {
            // 获取发送消息的玩家并回复
            Player player = event.getPlayer();
            CustomPopupEvent dialogEvent = new CustomPopupEvent(player, "test");
            dialogEvent.getVariables().put("yosinmessage"," ");
            dialogEvent.getVariables().put("yosinnpcavatar","\uDAC0\uDC01");
            dialogEvent.getVariables().put("yosinnpcname","测试NPC");
//            dialogEvent.getVariables().put("yosinvar1","你好");
            dialogEvent.getVariables().put("yosinvar2","告辞");
            dialogEvent.getVariables().put("yosinvar3","我想你误会了");
            dialogEvent.getVariables().put("yosinvar4","你说啥呢老弟");
            dialogEvent.getVariables().put("selectbuttom","sel");
            Popup dialogPopup = BetterHudAPI.inst().getPopupManager().getPopup("dialog_popup");
            Popup dialogoptions_popup = BetterHudAPI.inst().getPopupManager().getPopup("dialogoptions_popup");
            if (dialogPopup != null) {
                System.out.println("尝试展开对话");
                BukkitEventUpdateEvent updateEvent = new BukkitEventUpdateEvent(dialogEvent, "test1");

                String messageX= "你好啊,这是第一句话,这是第二句,而这是第三句,这是第五句话,这是AAAA话33333333333333!";
                List<String> strings = spiltX(messageX);
                HudPlayer hudPlayer = BetterHudAPI.inst().getPlayerManager().getHudPlayer(player.getUniqueId());
//                dialogoptions_popup.s
                this.sendMessagesWithDelay(player, strings, dialogPopup, dialogEvent, hudPlayer,updateEvent);
//                if (hudPlayer != null) {
//
//                    for (String str:strings){
//                        player.sendMessage(str);
//                        dialogEvent.getVariables().replace("yosinmessage",str);
//                        PopupUpdater show = dialogPopup.show(updateEvent, hudPlayer);
//                    }
//
//                }

            }
        }
    }


    public  void sendMessagesWithDelay(Player player, List<String> strings, Popup dialogPopup, CustomPopupEvent dialogEvent, HudPlayer hudPlayer,BukkitEventUpdateEvent updateEvent) {
      if (hudPlayer != null && strings != null && !strings.isEmpty()) {

            new BukkitRunnable() {
                private int index = 0; // 用来记录当前发送的消息的索引

                @Override
                public void run() {
                    if (index < strings.size()) {
                        // 发送消息给玩家
                        // player.sendMessage(strings.get(index));

                        //更新变量
                        dialogEvent.getVariables().replace("yosinmessage", strings.get(index));
                        dialogEvent.getVariables().replace("selectbuttom","select"+index%5);
                        //更新弹窗（Popup）
                        PopupUpdater show = dialogPopup.show(updateEvent, hudPlayer);
                        //增加索引，指向下一个消息
                        index++;
                    } else {
                        // 所有消息发送完毕后，停止任务
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(this, 0L, 3L); // 每隔 20 tick（1秒）发送一个消息
        }
    }




}
