#!/usr/bin/env bash
locale=$1

adb=${ANDROID_HOME}/platform-tools/adb

for i in {1..5};
do
  output=$(${adb} shell am broadcast -a com.android.intent.action.SET_LOCALE --es com.android.intent.extra.LOCALE "${locale}" com.android.customlocale2)
  if [[ "$output" == *"result=-1"* ]]; then
    if [[ "$locale" == "zh_CN" ]]; then
      # Disable Contacts and PinYin input for Chinese language variants
      # https://android.stackexchange.com/a/208271
      echo "Disabling contacts..."
      ${adb} shell pm disable-user com.android.contacts --user 0
      echo "Disabling pinyin keyboard..."
      ${adb} shell pm disable-user com.google.android.inputmethod.pinyin --user 0
    fi
    exit 0
  else
    echo "Attempt ${i} to set locale to ${locale} failed"
    sleep 2
  fi
done

echo "Unable to set locale! Exiting..."
exit 1