import vk_api;
import requests;

token = '171b8e8d7ebbfd17ea07041e8891613e257efe8185c70f520ab1e8b3236c8fec81fed32e77f13569c6722';
version = 5.92
domain = 'khm'

responce = requests.get('https://api.vk.com/method/groups.get',
                        params={
                            'access_token': token,
                            'v': version,
                            'domain': domain
                        })
print(responce.json())
