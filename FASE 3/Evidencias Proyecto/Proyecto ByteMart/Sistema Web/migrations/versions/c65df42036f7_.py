"""empty message

Revision ID: c65df42036f7
Revises: 
Create Date: 2024-11-19 13:22:35.159488

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'c65df42036f7'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('categoria',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=50), nullable=False),
    sa.Column('fecha_creacion', sa.DateTime(), nullable=False),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('name')
    )
    op.create_table('estado_orden',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('nombre', sa.String(length=50), nullable=False),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('nombre')
    )
    op.create_table('marca',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=100), nullable=False),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('name')
    )
    op.create_table('rol',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('nombre', sa.String(length=50), nullable=False),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('nombre')
    )
    op.create_table('usuario',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('username', sa.String(length=50), nullable=False),
    sa.Column('password', sa.String(length=100), nullable=False),
    sa.Column('nombre', sa.String(length=100), nullable=False),
    sa.Column('apellido', sa.String(length=100), nullable=False),
    sa.Column('email', sa.String(length=120), nullable=False),
    sa.Column('direccion', sa.String(length=100), nullable=True),
    sa.Column('reset_code', sa.String(length=6), nullable=True),
    sa.Column('reset_code_expiration', sa.DateTime(), nullable=True),
    sa.Column('is_admin', sa.Boolean(), nullable=True),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('username')
    )
    op.create_table('carrito',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('usuario_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['usuario_id'], ['usuario.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('direccion',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('usuario_id', sa.Integer(), nullable=False),
    sa.Column('nombre', sa.String(length=100), nullable=False),
    sa.Column('numero_domicilio', sa.String(length=10), nullable=False),
    sa.Column('comuna_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['usuario_id'], ['usuario.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('orden',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('usuario_id', sa.Integer(), nullable=False),
    sa.Column('estado_id', sa.Integer(), nullable=False),
    sa.Column('fecha_creacion', sa.DateTime(), nullable=False),
    sa.ForeignKeyConstraint(['estado_id'], ['estado_orden.id'], ),
    sa.ForeignKeyConstraint(['usuario_id'], ['usuario.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('producto',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=155), nullable=False),
    sa.Column('price', sa.Integer(), nullable=False),
    sa.Column('marca_id', sa.Integer(), nullable=False),
    sa.Column('descripcion', sa.String(length=1000), nullable=False),
    sa.Column('stock', sa.Integer(), nullable=False),
    sa.Column('category_id', sa.Integer(), nullable=False),
    sa.Column('en_oferta', sa.Boolean(), nullable=True),
    sa.Column('destacado', sa.Boolean(), nullable=True),
    sa.Column('fecha_creacion', sa.DateTime(), nullable=False),
    sa.ForeignKeyConstraint(['category_id'], ['categoria.id'], ),
    sa.ForeignKeyConstraint(['marca_id'], ['marca.id'], ),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('name')
    )
    op.create_table('tarjetas',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('usuario_id', sa.Integer(), nullable=False),
    sa.Column('numero_tarjeta', sa.String(length=16), nullable=False),
    sa.Column('mes_vencimiento', sa.Integer(), nullable=False),
    sa.Column('anio_vencimiento', sa.Integer(), nullable=False),
    sa.Column('codigo_verificacion', sa.String(length=3), nullable=False),
    sa.Column('saldo', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['usuario_id'], ['usuario.id'], ),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('numero_tarjeta', 'usuario_id', name='unique_tarjeta_usuario')
    )
    op.create_table('usuario_rol',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('usuario_id', sa.Integer(), nullable=False),
    sa.Column('rol_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['rol_id'], ['rol.id'], ),
    sa.ForeignKeyConstraint(['usuario_id'], ['usuario.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('factura',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('orden_id', sa.Integer(), nullable=False),
    sa.Column('monto', sa.Integer(), nullable=False),
    sa.Column('fecha_creacion', sa.DateTime(), nullable=False),
    sa.ForeignKeyConstraint(['orden_id'], ['orden.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('imagen',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('image_url', sa.String(length=255), nullable=False),
    sa.Column('producto_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['producto_id'], ['producto.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('item_carrito',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('carrito_id', sa.Integer(), nullable=False),
    sa.Column('producto_id', sa.Integer(), nullable=False),
    sa.Column('cantidad', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['carrito_id'], ['carrito.id'], ),
    sa.ForeignKeyConstraint(['producto_id'], ['producto.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('orden_item',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('orden_id', sa.Integer(), nullable=False),
    sa.Column('producto_id', sa.Integer(), nullable=False),
    sa.Column('cantidad', sa.Integer(), nullable=False),
    sa.Column('fecha_creacion', sa.DateTime(), nullable=False),
    sa.ForeignKeyConstraint(['orden_id'], ['orden.id'], ),
    sa.ForeignKeyConstraint(['producto_id'], ['producto.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('proceso_pago',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('tarjeta_id', sa.Integer(), nullable=False),
    sa.Column('fecha_pago', sa.DateTime(), nullable=False),
    sa.Column('monto', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['tarjeta_id'], ['tarjetas.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('proceso_pago')
    op.drop_table('orden_item')
    op.drop_table('item_carrito')
    op.drop_table('imagen')
    op.drop_table('factura')
    op.drop_table('usuario_rol')
    op.drop_table('tarjetas')
    op.drop_table('producto')
    op.drop_table('orden')
    op.drop_table('direccion')
    op.drop_table('carrito')
    op.drop_table('usuario')
    op.drop_table('rol')
    op.drop_table('marca')
    op.drop_table('estado_orden')
    op.drop_table('categoria')
    # ### end Alembic commands ###
