#!/usr/bin/env bash
cmd=$1

if [[ $cmd != "on" && $cmd != "off" ]]; then
  echo "You must specifiy on or off for the script to execute"
  echo "pipeline/./emulator-demo-mode.sh [on|off]"
  exit
fi

adb=${ANDROID_HOME}/platform-tools/adb

# Running the script with any arguments passed in will exit demo mode
if [[ $cmd == "off" ]]; then
"${adb}" shell am broadcast -a com.android.systemui.demo -e command settings put global location_providers_allowed +gps
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command settings put global location_providers_allowed +network
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command settings put secure icon_blacklist location
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command exit
  echo "Emulator has exited demo mode"
else
  "${adb}" shell settings put global sysui_demo_allowed 1
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command enter
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1231
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command network -e mobile show -e level 4
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged true
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command settings put global location_providers_allowed -gps
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command settings put global location_providers_allowed -network
  "${adb}" shell am broadcast -a com.android.systemui.demo -e command settings put secure icon_blacklist location
  echo "Emulator in demo mode"
fi