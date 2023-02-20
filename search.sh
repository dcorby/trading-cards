google test site: https://developers.google.com/custom-search/v1/introduction/?apix=true
search engine instances: https://programmablesearchengine.google.com/cse/all (get cx value)
metrics: https://console.cloud.google.com/apis/api/customsearch.googleapis.com/metrics?project=trading-cards-378400
dashboard: https://console.cloud.google.com/home/dashboard?q=search&referrer=search&project=trading-cards-378400
resource manager: https://console.cloud.google.com/cloud-resource-manager

curl \
  'https://customsearch.googleapis.com/customsearch/v1?imgType=photo&q=rickey+henderson+baseball&searchType=image&cx=[cx]&key=[key]' \
  --header 'Accept: application/json' \
  --compressed

