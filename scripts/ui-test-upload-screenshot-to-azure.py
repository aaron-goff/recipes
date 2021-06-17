import sys
import base64
import os.path
import os
import requests
import json

# This is a python script. Do not run it in bash.

# Get the arguments for the script
team_project = sys.argv[1]
auth_token = sys.argv[2]
build_uri = sys.argv[3]
organization = sys.argv[4]
flavor = sys.argv[5]
locale = sys.argv[6]

# Convert string of `email:pat` into encoded auth token
# ex: my.email@email.com:myAzureDevopsGeneratedPAT
# If you are getting 203 errors when debugging locally, you are probably just passing in the PAT
token_bytes = auth_token.encode('ascii')
encoded_auth_token = base64.b64encode(token_bytes)
encoded_auth_token_string = encoded_auth_token.decode('ascii')

# Define base request variables
base_url = f"https://dev.azure.com/{organization}/{team_project}/_apis"
header = {"content-type": "application/json; charset=utf-8", "Authorization": f"Basic {encoded_auth_token_string}"}
api_version = "api-version=5.1"

# Define base screenshot directory variables
screenshots_dir = ""
flavors_dir = "app/build/reports/androidTests/connected/flavors/"
for screenshot_dir in os.listdir(flavors_dir):
    if flavor.lower() in str(screenshot_dir).lower():
        screenshots_dir = f"{flavors_dir}{str(screenshot_dir)}/{locale}"
        print(f"Screenshot Directory is: {screenshots_dir}")
        break
if screenshots_dir == "":
    sys.exit("Screenshots Directory not found!")

# Get run_ids for current build
api = f'{base_url}/test/runs?buildUri={build_uri}&{api_version}'
response = requests.get(api, headers=header)
print("GET", api, "-->", response.status_code)
if response.status_code != 200:
    print('''
        !!! ERROR - Response was not 200.
        Response code was {code}.
        Response body was {body}
        '''.format(code=response_failures.status_code, body=response_failures.text))
    sys.exit("Unable to get test run information")
json_response = json.loads(response.text)['value']
run_id = []
compare_run_id = []
for test_result in json_response:
    if "UI Tests" in test_result['name'] or "Translation" in test_result['name']:
        run_id.append(test_result['id'])
    elif "Compare Images" in test_result['name']:
        compare_run_id.append(test_result['id'])
# Get results for current build, as an array of [run_id, url]
get_results_api = []
for run in run_id:
    get_results_api.append([run, f'{base_url}/test/runs/{run}/results?{api_version}&outcomes='])

get_compare_results_api = []
for compare_run in compare_run_id:
    get_compare_results_api.append(
        [compare_run, f'{base_url}/test/runs/{compare_run}/results?{api_version}&outcomes=2,3'])


def upload(upload_img_path, upload_result_id, upload_test_name, upload_run_id):
    print("Screenshot exists, will upload: ", upload_img_path)
    with open(upload_img_path, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read())
        upload_api = f'{base_url}/test/runs/{upload_run_id}/results/{upload_result_id}/attachments?{api_version}-preview.1'
        data = {'stream': encoded_string.decode('utf-8'), 'fileName': f'{upload_test_name}.png',
                'comment': 'Uploaded by REST from pipeline', 'attachmentType': 'GeneralAttachment'}
        upload_response = requests.post(upload_api, headers=header, json=data)
        print("POST", upload_api, "-->", upload_response.status_code)
        if upload_response.status_code != 200:
            print('''
            !!! POST Failed -- debug info coming !!!
            fileName: {upload_test_name}.png
            upload_response.text: {upload_response_text}
            '''.format(upload_test_name=upload_test_name, upload_response_text=upload_response.text))


# Re-usable function to upload image attachment
def upload_attachment(img_path, result_id, test_name, run_id):
    # Only execute if we can find the image
    if os.path.isfile(img_path):
        upload(img_path, result_id, test_name, run_id)
    else:
        print("Screenshot does not exist, will not upload")
        print("Image Path is: ", img_path)

    img = img_path.split("/").pop()
    ad_hoc_img_path = ""
    if "successes" in img_path:
        ad_hoc_img_path = img_path.replace(img, "").replace("successes", "ad hoc")
    elif "failures" in img_path:
        ad_hoc_img_path = img_path.replace(img, "").replace("failures", "ad hoc")

    if os.path.isdir(ad_hoc_img_path):
        for image in os.listdir(ad_hoc_img_path):
            if img in image:
                upload_attachment(f"{ad_hoc_img_path}{image}", result_id, image.split(".")[0], run_id)


# Get failed tests and upload screenshot
for result in get_results_api:
    api_failures = f'{result[1]}3'
    response_failures = requests.get(api_failures, headers=header)
    print("GET", api_failures, "-->", response_failures.status_code)
    if response_failures.status_code == 200:
        failures = json.loads(response_failures.text)['value']
        print("failures is:\n", str(failures))
        for failed_test in failures:
            result_id = failed_test['id']
            class_name = failed_test['automatedTestStorage']
            test_name = failed_test['testCaseTitle']
            img_path = f"{screenshots_dir}/failures/{class_name}/{test_name}.png"
            print('imgpath = ' + img_path)
            upload_attachment(img_path, result_id, test_name, result[0])
    else:
        print('''
        !!! ERROR - Response was not 200.
        Response code was {code}.
        Response body was {body}
        '''.format(code=response_failures.status_code, body=response_failures.text))

    # Get successful tests and upload screenshots
    api_successes = f'{result[1]}2'
    response_successes = requests.get(api_successes, headers=header)
    print("GET", api_successes, "-->", response_successes.status_code)
    if response_successes == 200:
        successes = json.loads(response_successes.text)['value']
        print("successes is:\n", str(successes))
        for successful_test in successes:
            result_id = successful_test['id']
            class_name = successful_test['automatedTestStorage']
            test_name = successful_test['testCaseTitle']
            img_path = f"{screenshots_dir}/successes/{class_name}/{test_name}.png"
            upload_attachment(img_path, result_id, test_name, result[0])
    else:
        print('''
        !!! ERROR - Response was not 200.
        Response code was {code}.
        Response body was {body}
        '''.format(code=response_successes.status_code, body=response_successes.text))

for result in get_compare_results_api:
    compare_response = requests.get(result[1], headers=header)
    print("GET", result[1], "-->", compare_response.status_code)
    if compare_response == 200:
        comparisons = json.loads(compare_response.text)['value']
        print("comparisons are:\n", str(comparisons))
        for comparison in comparisons:
            result_id = comparison['id']
            full_test_name = comparison['testCaseTitle']
            full_image_name = full_test_name.split('[')[1].replace(']', '')
            image_name = full_image_name.split(".png")[0]
            image_name_with_png = image_name + ".png"
            image_name_no_slash = image_name.split("/")[1]
            img_path = f"{screenshots_dir}/comparisons/{image_name_with_png}"
            upload_attachment(img_path, result_id, image_name_no_slash, result[0])
    else:
        print('''
        !!! ERROR - Response was not 200.
        Response code was {code}.
        Response body was {body}
        '''.format(code=compare_response.status_code, body=compare_response.text))
