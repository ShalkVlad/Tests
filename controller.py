import time
from typing import Dict, Any
from flask import Flask, jsonify, request, render_template, Response

app = Flask(__name__)

tasks = [
    {
        'id': 1,
        'title': 'Покупки',
        'description': 'Купить продукты в магазине',
        'creation_date': '2023-11-08',
        'attachments': [],
        'categories': []
    },
    {
        'id': 2,
        'title': 'Уборка',
        'description': 'Почистить квартиру',
        'creation_date': '2023-11-20',
        'attachments': [],
        'categories': ['Домашние дела']
    }
]


@app.route('/')
def index():
    return render_template('site.html')


@app.route('/tasks', methods=['POST'])
def create_task() -> Response:
    data: Dict[str, Any] = request.get_json()
    current_date: str = time.strftime('%Y-%m-%d')
    new_task: Dict[str, Any] = {
        'id': len(tasks) + 1,
        'title': data['title'],
        'description': data['description'],
        'creation_date': current_date,
        'attachments': data.get('attachments', []),
        'categories': data.get('categories', []),
        'priority': data.get('priority', []),
    }
    tasks.append(new_task)
    response: Response = jsonify({'task': new_task})
    response.status_code = 201
    return response


# Получение списка всех задач
@app.route('/tasks', methods=['GET'])
def get_all_tasks():
    return jsonify({'tasks': tasks})


# Получение задачи по идентификатору
@app.route('/tasks/<int:task_id>', methods=['GET'])
def get_task(task_id):
    task = next((task for task in tasks if task['id'] == task_id), None)
    if task is None:
        return jsonify({'error': 'Задача не найдена'}), 404
    return jsonify({'task': task})


# Обновление задачи
@app.route('/tasks/<int:task_id>', methods=['PUT'])
def update_task(task_id):
    task = next((task for task in tasks if task['id'] == task_id), None)
    if task is None:
        return jsonify({'error': 'Задача не найдена'}), 404

    data = request.get_json()
    task['title'] = data.get('title', task['title'])
    task['description'] = data.get('description', task['description'])
    task['attachments'] = data.get('attachments', task['attachments'])
    task['categories'] = data.get('categories', task['categories'])
    if 'priority' in data:
        task['priority'] = data['priority']
    return jsonify({'task': task})


# Удаление задачи
@app.route('/tasks/<int:task_id>', methods=['DELETE'])
def delete_task(task_id):
    global tasks
    tasks = [task for task in tasks if task['id'] != task_id]
    return jsonify({'result': True})


if __name__ == '__main__':
    app.run(debug=True, port=os.getenv("PORT", default=5000))
