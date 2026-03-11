package com.azyu.mahallavote.bot;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public final class KeyboardHelper {

    private KeyboardHelper() {}

    public static ReplyKeyboardMarkup mainMenu() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(BotConstants.BTN_VOTE);
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(BotConstants.BTN_BALANCE);
        row2.add(BotConstants.BTN_WITHDRAW);
        rows.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(BotConstants.BTN_REFERRAL);
        row3.add(BotConstants.BTN_PRIZE);
        rows.add(row3);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        return markup;
    }

    public static ReplyKeyboardMarkup phoneRequestKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        KeyboardButton phoneButton = new KeyboardButton(BotConstants.BTN_SEND_PHONE);
        phoneButton.setRequestContact(true);
        row.add(phoneButton);
        rows.add(row);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        return markup;
    }
}
