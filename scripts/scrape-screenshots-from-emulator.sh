#!/usr/bin/env bash
flavor=$1
langLocale=$2
if [[ -z "${flavor}" ]]; then
  echo "No flavor provided! Exiting..."
  exit 0
else
  echo "Flavor is ${flavor}"
fi

if [[ -z "${langLocale}" ]]; then
  langLocale="en_US"
fi
echo "Language-locale combination is ${langLocale}"

# Declare variables
reportsDir="app/build/reports/androidTests/connected/flavors"
flavorResultsDir=""
for dir in "${reportsDir}"/*
do
  echo "original dirName is ${dir}"
  dirName=$(echo "${dir}" | tr [:lower:] [:upper:])
  echo "upper dirName is ${dirName}"
  newFlavor=$(echo "${flavor}" | tr [:lower:] [:upper:])
  echo "upper newFlavor is ${newFlavor}"
  if [[ ${dirName} =~ ${newFlavor} ]]; then
    formatDirName=$(basename "${dir}")
    flavorResultsDir="${reportsDir}/${formatDirName}"
    break
  fi
done
if [[ "$flavorResultsDir" == "" ]]; then
  echo "Unable to find Results directory for ${flavor}"
  echo "Creating Flavor Results directory"
  flavorResultsDir="$reportsDir/cobrandedMockDebugAndroidTest"
fi
echo "Results directory for ${flavor} is ${flavorResultsDir}"

screenshotsDir='/sdcard/Pictures/Automation'
adb="${ANDROID_HOME}/platform-tools/adb"
successDir="${flavorResultsDir}/${langLocale}/successes"
failureDir="${flavorResultsDir}/${langLocale}/failures"
adHocDir="${flavorResultsDir}/${langLocale}/ad hoc"
screenshotDirs=("${successDir}" "${failureDir}" "${adHocDir}")

# Create screenshot directory on device
"${adb}" shell mkdir -p "${screenshotsDir}"

# Create reports directory
if [[ ! -d "${flavorResultsDir}" ]]; then
  echo "Creating ${flavorResultsDir}"
  mkdir -p "${flavorResultsDir}"
fi

# Create locale directory
echo "Creating ${flavorResultsDir}/${langLocale}"
mkdir "${flavorResultsDir}/${langLocale}"

# Fetch screenshots from device
"${adb}" pull "${screenshotsDir}/failures" "${failureDir}"
"${adb}" pull "${screenshotsDir}/successes" "${successDir}"
"${adb}" pull "${screenshotsDir}/ad hoc" "${adHocDir}"

# Clear screenshots from device
echo "Clearing Screenshots from device"
"${adb}" shell rm -r "${screenshotsDir}"

# Create screenshots directories
for screenshotDir in "${screenshotDirs[@]}"
do
  echo "Creating ${screenshotDir}"
  mkdir -p "${screenshotDir}"
done

for screenshotDir in "${screenshotDirs[@]}"
do
  if [[ ! -d "${screenshotDir}" ]]; then
  echo "${screenshotDir} does not exist! Skipping..."
  exit 0
fi
done

renamedFilesString=""

function renameFiles {
  dirToUse=$1

  for class in "${dirToUse}"/*
  do
    className=$(basename "${class}")
    if [[ ${className} == "*" ]]; then
      break
    fi
    for file in "${dirToUse}/${className}"/*
    do
      fileName=$(basename "${file}")
      fileNameNoExt=$(echo "${fileName}" | cut -d'-' -f 1)
      mv "${dirToUse}/${className}/${fileName}" "${dirToUse}/${className}/${fileNameNoExt}.png"
      renamedFilesString+="-${dirToUse}-"
    done
  done
}

for screenshotDir in "${screenshotDirs[@]}"
do
  echo "Renaming files in ${screenshotDir}"
  renameFiles "${screenshotDir}"
done

# Create PDFs
if [[ "${renamedFilesString}" =~ .*"${failureDir}".* ]]; then
 magick convert "${failureDir}"/**/*.png -resize 50% "${flavorResultsDir}"/"${langLocale}"/failureScreenshots.pdf
fi

if [[ "${renamedFilesString}" =~ .*"${successDir}".* ]]; then
  magick convert "${successDir}"/**/*.png -resize 50% "${flavorResultsDir}"/"${langLocale}"/successScreenshots.pdf
fi

if [[ "${renamedFilesString}" =~ .*"${adHocDir}".* ]]; then
  magick convert "${adHocDir}"/**/*.png -resize 50% "${flavorResultsDir}"/"${langLocale}"/adHocsScreenshots.pdf
fi