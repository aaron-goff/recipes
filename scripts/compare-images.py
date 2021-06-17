import sys
import os.path
import os
from wand.image import Image
import pytest

# Make sure a flavor is passed in
# Currently not passed in, but leaving logic in case it changes
flavor='Cobranded'

if flavor == "-f":
    print("Error - No flavor provided. Exiting...")
    sys.exit()
else:
    print("Flavor is: ", str(flavor))

# Declare directory paths
baseline_dir=f'app/src/androidTest/resources/baselines/{flavor}'
print(f'baseline_dir is {baseline_dir}')
reports_dir=f'app/build/reports/androidTests/connected/flavors'
new_flavor=''
screenshots_dir=''
if os.path.isdir(reports_dir):
    for directory in os.listdir(reports_dir):
        if flavor.lower() in directory.lower():
            new_flavor=directory
            print(f'new_flavor is {new_flavor}')
            break

if new_flavor == '':
    print("Error - Unable to find screenshots directory. Exiting...")
    sys.exit()
else:
    screenshots_dir=f'{reports_dir}/{new_flavor}/en_US'
    print(f'screenshots_dir is {screenshots_dir}')

comparisons_dir=f'{screenshots_dir}/comparisons'

# Create comparisons directory
if os.path.exists(comparisons_dir) is False:
    os.makedirs(comparisons_dir)

# Collect
baseline_images={}
if os.path.isdir(baseline_dir):
    for directory in os.listdir(baseline_dir):
        if os.path.isdir(f'{baseline_dir}/{directory}'):
            images=[]
            for image in os.listdir(f'{baseline_dir}/{directory}'):
                images.append(image)
            baseline_images[directory]=images

def image_comparison(base_filename, img_filename, class_name, diff_filename):
    with Image(filename=base_filename) as base:
        with Image(filename=img_filename) as img:
            base.fuzz = base.quantum_range * 0.25
            result_image, result_metric = base.compare(img, 'absolute')
            if result_metric > 0:
                with result_image:
                    if os.path.exists(f'{comparisons_dir}/{class_name}') is False:
                        os.mkdir(f'{comparisons_dir}/{class_name}')
                    result_image.save(filename=f'{comparisons_dir}/{class_name}/{diff_filename}')
            return result_metric

def get_results():
    results_array=[]
    for directory in baseline_images:
        for image in baseline_images[directory]:
            if "_" in image:
                search_dir=f'{screenshots_dir}/ad hoc/{directory}'
            else:
                search_dir=f'{screenshots_dir}/successes/{directory}'

            if image in os.listdir(search_dir):
                diff=image_comparison(f'{baseline_dir}/{directory}/{image}',
                                      f'{search_dir}/{image}',
                                      directory,
                                      image)
            else:
                diff=1

            results_array.append((f'{directory}/{image}', diff))
    return results_array

results=get_results()

# comparing images is not working as expected right now, so disabling
@pytest.mark.parametrize("image_name,diff_result", results)
def test_diff_images(image_name, diff_result):
    # assert diff_result is not None
    assert diff_result == 0, f'Failure! {image_name} had a diff of {diff_result}'