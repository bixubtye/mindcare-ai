INSERT IGNORE INTO `knowledge_category`
  (`id`, `parent_id`, `category_name`, `category_code`, `description`, `sort_order`, `status`)
VALUES
  (1, 0, '情绪管理', 'emotion', '认识情绪并学习日常调节方法', 1, 1),
  (2, 0, '压力应对', 'stress', '建立更健康的压力应对方式', 2, 1),
  (3, 0, '睡眠健康', 'sleep', '改善睡眠习惯与睡眠质量', 3, 1);

INSERT IGNORE INTO `knowledge_article`
  (`id`, `category_id`, `title`, `summary`, `content`, `tags`, `status`, `published_at`)
VALUES
  ('demo-emotion-001', 1, '给情绪一个名字', '准确识别情绪，是调节情绪的第一步。',
   '<h2>从觉察开始</h2><p>当情绪出现时，先暂停片刻，尝试说出它的名字：焦虑、委屈、愤怒，或只是疲惫。</p><p>命名不会让情绪立刻消失，但会帮助我们从被情绪裹挟，转向观察和理解它。</p>',
   '情绪觉察,自我关怀', 1, NOW()),
  ('demo-stress-001', 2, '一分钟呼吸练习', '用简单的呼吸节奏，为紧绷的身体留出一点空间。',
   '<h2>试试 4-4 呼吸</h2><p>缓慢吸气四秒，再缓慢呼气四秒。重复一分钟，不必追求完美，只需留意空气经过鼻腔和胸腹的感觉。</p>',
   '压力管理,呼吸练习', 1, NOW()),
  ('demo-sleep-001', 3, '睡前缓冲区', '给忙碌的一天一个温和的结束。',
   '<h2>建立固定的睡前信号</h2><p>睡前半小时降低灯光亮度，暂时离开高刺激内容，并把明天要处理的事情写在纸上。</p>',
   '睡眠,生活习惯', 1, NOW());
