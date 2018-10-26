#encoding:utf-8
from socket import socket,AF_INET,SOCK_STREAM
import time
import RPi.GPIO as GPIO
import sys,os,cv2
import numpy as np
import datetime
import time
import atexit

atexit.register(GPIO.cleanup)

def init():
    GPIO.setup(IN1, GPIO.OUT)
    GPIO.setup(IN2, GPIO.OUT)
    GPIO.setup(IN3, GPIO.OUT)
    GPIO.setup(IN4, GPIO.OUT)


def go():
    GPIO.output(IN1, GPIO.HIGH)
    GPIO.output(IN2, GPIO.LOW)
    GPIO.output(IN3, GPIO.HIGH)
    GPIO.output(IN4, GPIO.LOW)
    if get_distance() < 20:
        stop()
    a = get_distance()
    print a

def back():
    GPIO.output(IN1, GPIO.LOW)
    GPIO.output(IN2, GPIO.HIGH)
    GPIO.output(IN3, GPIO.LOW)
    GPIO.output(IN4, GPIO.HIGH)



def TurnRight():
    GPIO.output(IN1, GPIO.HIGH)
    GPIO.output(IN2, GPIO.LOW)
    GPIO.output(IN3, False)
    GPIO.output(IN4, False)


def TurnLeft():
    GPIO.output(IN1, False)
    GPIO.output(IN2, False)
    GPIO.output(IN3, GPIO.HIGH)
    GPIO.output(IN4, GPIO.LOW)



def stop():
    GPIO.output(IN1,  False)
    GPIO.output(IN2,  False)
    GPIO.output(IN3,  False)
    GPIO.output(IN4,  False)



def send_trigger_pulse():
    GPIO.output(trigger_pin, True)
    time.sleep(0.0001)
    GPIO.output(trigger_pin, False)


def wait_for_echo(value, timeout):
    count = timeout
    while GPIO.input(echo_pin) != value and count > 0:
        count = count - 1


def get_distance():
    send_trigger_pulse()
    wait_for_echo(True, 10000)
    start = time.time()
    wait_for_echo(False, 10000)
    finish = time.time()
    pulse_len = finish - start
    distance_cm = pulse_len * 340 / 2 * 100
    return distance_cm


def read_images(path, sz=None):
    c = 0
    X, y = [], []
    for dirname, dirnames, filenames in os.walk(path):
        for subdirname in dirnames:
            subject_path = os.path.join(dirname, subdirname)
            for filename in os.listdir(subject_path):
                try:
                    filepath = os.path.join(subject_path, filename)
                    if os.path.isdir(filepath):
                        continue
                    img = cv2.imread(os.path.join(subject_path, filename), cv2.IMREAD_GRAYSCALE)
                    if (img is None):
                        print("image " + filepath + " is none")
                    else:
                        print(filepath)
                    if (sz is not None):
                        img = cv2.resize(img, (200, 200))

                    X.append(np.asarray(img, dtype=np.uint8))
                    y.append(c)
                except:
                    print("Unexpected error:", sys.exc_info()[0])
                    raise
            print(c)
            c = c + 1

    print(y)
    return [X, y]


def daka():
    names = ['lxt', 'ztt', 'lx']
    if len(sys.argv) < 2:
        print("USAGE: facerec.py <path wrong> [<table wrong>]")
        sys.exit()

    [X, y] = read_images(sys.argv[1])
    model = cv2.createFisherFaceRecognizer()
    model.train(np.array(X), np.array(y))

    camera = cv2.VideoCapture(0)
    print ('open!')
    file = r'./records.txt'
    face_cascade = cv2.CascadeClassifier('/usr/share/opencv/lbpcascades/lbpcascade_frontalface.xml')
    flag = 0
    while (True):
        read, img = camera.read()
        faces = face_cascade.detectMultiScale(img, 1.3, 5)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        for (x, y, w, h) in faces:
            cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
            roi = gray[x:x + w, y:y + h]
            roi = cv2.resize(roi, (200, 200), interpolation=cv2.INTER_LINEAR)
            params = model.predict(roi)
            print("Label: %s, Confidence: %.2f" % (params[0], params[1]))
            cv2.putText(img, names[params[0]], (x, y - 20), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)
            cv2.imwrite('face_rec.jpg', img)

            nowTime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            with open(file, 'a+') as f:
                f.write(names[params[0]] +' '+ nowTime + '\n')
            print ('welcome!')
            flag = 1
            break
        if flag == 1:
            break
        if cv2.waitKey(120) & 0xff == ord("q"):
            break

    camera.release()
    cv2.destroyAllWindows()



if __name__ == "__main__":
    import sys;
    reload(sys);
    sys.setdefaultencoding("utf8")

    IN1 = 11
    IN2 = 12
    IN3 = 13
    IN4 = 15
    trigger_pin = 16
    echo_pin = 18
    IN6 = 22
    IN7 = 7
    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(trigger_pin,GPIO.OUT)
    GPIO.setup(echo_pin,GPIO.IN)
    GPIO.setup(IN6, GPIO.OUT, initial=False)
    p = GPIO.PWM(IN6, 50)  # 50HZ
    p.start(0)
    GPIO.setup(IN7, GPIO.OUT, initial=False)
    p1= GPIO.PWM(IN7, 50)  # 50HZ
    p1.start(0)
    init()

    #服务端的ip地址
    #server_ip = "47.93.252.130"
    server_ip = "192.168.43.181"
    #服务端socket绑定的端口号
    server_port = 2222

    client = socket(AF_INET, SOCK_STREAM)
    client.connect((server_ip, server_port))
    client.send("123".encode())
    while (1):
        msg = client.recv(1024).decode('utf-8')
        print(msg)
        print (type(msg))
        if str(msg)=="上".encode('utf8'):
            print 'go is already'
            go()
        if str(msg) == "下".encode('utf8'):
            back()
        if str(msg) == "左".encode('utf8'):
            TurnLeft()
        if str(msg) == "右".encode('utf8'):
            TurnRight()
        if str(msg) == "抓取".encode('utf8'):
            daka()
        if str(msg) == "松开".encode('utf8'):
            p.ChangeDutyCycle(8.5)
            p1.ChangeDutyCycle(8.5)
            time.sleep(4)
            p1.ChangeDutyCycle(8.5)
            p.ChangeDutyCycle(11.5)
            time.sleep(10)
        if str(msg) == "停止".encode('utf8'):
            stop()
